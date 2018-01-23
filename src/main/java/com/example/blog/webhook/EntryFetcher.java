package com.example.blog.webhook;

import java.nio.file.Paths;

import am.ik.blog.entry.*;
import am.ik.blog.entry.factory.EntryFactory;
import am.ik.github.GitHubClient;
import am.ik.github.core.Committer;
import am.ik.github.repositories.commits.CommitsResponse.Commit;
import am.ik.github.repositories.contents.ContentsResponse.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class EntryFetcher {
	private final GitHubClient gitHubClient;

	public EntryFetcher(GitHubClient gitHubClient) {
		this.gitHubClient = gitHubClient;
	}

	public Mono<Entry> fetch(String owner, String repo, String path) {
		EntryId entryId = EntryId.fromFilePath(Paths.get(path));
		Mono<File> file = this.gitHubClient.file(owner, repo, path).get();
		Flux<Commit> commits = this.gitHubClient.commits(owner, repo)
				.get(params -> params.path(path));

		// TODO: 実装してください

		Mono<Entry.EntryBuilder> builder = file
				.map(f -> new EntryFactory().parseBody(entryId, f.decode()))
				.flatMap(b -> Mono.justOrEmpty(b));

		Mono<Tuple2<Author, Author>> authors = commits.collectList()
				.map(list -> {
					Author updated = toAuthor(list.get(0));
					Author created = toAuthor(list.get(list.size() - 1));
					return Tuples.of(created, updated);
				});

		return Mono.zip(builder, authors)
				.map(t -> {
					Entry.EntryBuilder entryBuilder = t.getT1();
					Author created = t.getT2().getT1();
					Author updated = t.getT2().getT2();
					return entryBuilder
							.created(created)
							.updated(updated)
							.build()
							.useFrontMatterDate();
				});
	}

	private static Author toAuthor(Commit commit) {
		Committer committer = commit.getCommit().getAuthor();
		return new Author(new Name(committer.getName()), new EventTime(committer.getDate()));
	}
}
