package com.example.blog;

import java.time.OffsetDateTime;
import java.util.Optional;

import am.ik.blog.entry.*;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

public class EntryCreatorTest {

	@Test
	public void builderTest() throws Exception {
		OffsetDateTime now = OffsetDateTime.now();
		Entry entry = EntryCreator.createEntry(new EntryId(1L), new Content("記事本文"),
				new Title("タイトル"),
				new Categories(new Category("カテゴリ1"), new Category("カテゴリ2")),
				new Tags(new Tag("タグ1"), new Tag("タグ2")),
				new Author(new Name("作成者名"), new EventTime(now)),
				new Author(new Name("更新者名"), new EventTime(now)));

		assertThat(entry).isNotNull();
		assertThat(entry.getEntryId()).isEqualTo(new EntryId(1L));
		assertThat(entry.getContent()).isEqualTo(new Content("記事本文"));
		assertThat(entry.getCreated())
				.isEqualTo(new Author(new Name("作成者名"), new EventTime(now)));
		assertThat(entry.getUpdated())
				.isEqualTo(new Author(new Name("更新者名"), new EventTime(now)));

		FrontMatter frontMatter = entry.getFrontMatter();
		assertThat(frontMatter).isNotNull();
		assertThat(frontMatter.title()).isEqualTo(new Title("タイトル"));
		assertThat(frontMatter.categories())
				.isEqualTo(new Categories(new Category("カテゴリ1"), new Category("カテゴリ2")));
		assertThat(frontMatter.tags())
				.isEqualTo(new Tags(new Tag("タグ1"), new Tag("タグ2")));
	}

	@Test
	public void factoryTest() throws Exception {
		OffsetDateTime now = OffsetDateTime.now();
		Resource resource = new ClassPathResource("00001.md");
		Author created = new Author(new Name("作成者名"), new EventTime(now));
		Author updated = new Author(new Name("更新者名"), new EventTime(now));

		Optional<Entry> opt = EntryCreator.createEntryFromMarkdown(resource, created,
				updated);
		assertThat(opt).isNotNull();
		assertThat(opt.isPresent()).isTrue();
		Entry entry = opt.get();
		assertThat(opt.isPresent()).isTrue();

		assertThat(entry.getEntryId()).isEqualTo(new EntryId(1L));
		assertThat(entry.getContent())
				.isEqualTo(new Content("This is my first blog post!"));
		assertThat(entry.getCreated())
				.isEqualTo(new Author(new Name("作成者名"), new EventTime(now)));
		assertThat(entry.getUpdated())
				.isEqualTo(new Author(new Name("更新者名"), new EventTime(now)));

		FrontMatter frontMatter = entry.getFrontMatter();
		assertThat(frontMatter).isNotNull();
		assertThat(frontMatter.title()).isEqualTo(new Title("First article"));
		assertThat(frontMatter.categories())
				.isEqualTo(new Categories(new Category("Demo"), new Category("Hello")));
		assertThat(frontMatter.tags()).isEqualTo(new Tags(new Tag("Demo")));
	}
}