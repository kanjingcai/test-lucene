package com.test.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestIndex {

	/**
	 * 创建中文分词索引库
	 * @throws Exception
	 */
	@Test
	public void testAddIndexIK() throws Exception {
		List<Document> documents = new ArrayList<Document>();
		
		for (int i = 0; i < 100; i++) {
			//创建文档，商品数据
			Document doc = new Document();
			doc.add(new LongField("id", Long.valueOf(i + 1), Store.YES));
			//TextField:做索引并分词
			doc.add(new TextField("title", i + "我爱爪哇 Apple/苹果 iPhone "+ i +" Plus 5.5屏 移动4G联通电信 2G "+i+"G 银白", Store.YES));
			doc.add(new StringField("image", "https://img.alicdn.com/tps/TB1G8PcHFXXXXarapXXXXXXXXXX-236-110.gif", Store.YES));
			doc.add(new IntField("status", 1, Store.YES));
			documents.add(doc);
		}
		
		
		//创建文件系统的位置
		Directory dir = FSDirectory.open(new File("index"));
		 //定义IK分词器
		Analyzer analyzer = new IKAnalyzer();
		//索引写入对象的配置
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
		
		//先删除原有索引，再写入，默认OpenMode.APPEND
		indexWriterConfig.setOpenMode(OpenMode.CREATE);
		
		//索引写入对象
		IndexWriter writer = new IndexWriter(dir,indexWriterConfig);
		
		//写入
		writer.addDocuments(documents);
		
		writer.close();
	}
}
