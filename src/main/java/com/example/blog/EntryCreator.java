package com.example.blog;

import java.time.OffsetDateTime;
import java.util.Optional;

import am.ik.blog.entry.*;
import am.ik.blog.entry.factory.EntryFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class EntryCreator {
	public static Entry createEntry(EntryId entryId, Content content, Title title,
			Categories categories, Tags tags, Author created, Author updated) {
		// TODO: 実装してください
		return Entry.builder()
				.entryId(entryId)
				.content(content)
				.created(created)
				.updated(updated)
				.frontMatter(new FrontMatter(title, categories, tags))
				.build();
	}

	public static Optional<Entry> createEntryFromMarkdown(Resource resource,
			Author created, Author updated) {
		EntryFactory factory = new EntryFactory();
		// TODO: 実装してください
		Resource file = new ClassPathResource("00001.md"); // クラスパス上のMarkdownファイルから作成
		Optional<Entry.EntryBuilder> entryBuilder = factory.createFromYamlFile(file);
		return entryBuilder.map(
				builder -> builder.entryId(EntryId.fromFileName(file.getFilename()))
						.created(created)
						.updated(updated)
						.build());
	}
}
