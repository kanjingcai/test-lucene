package com.test.lucene;


import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * lucene入门
 * @author kanjingcai@gmail.com 
 *
 */
public class TestLucene {

	
	/**
	 * 创建索引库
	 * @throws Exception
	 */
	@Test
	public void testAddIndex() throws Exception {
		//创建文档，商品数据
		Document doc = new Document();
		doc.add(new LongField("id", 12, Store.YES));
		//TextField:做索引并分词
		doc.add(new TextField("title", "Apple/苹果 iPhone 6 Plus 5.5屏 移动4G联通电信4G 16G 银白", Store.YES));
		doc.add(new StringField("image", "https://img.alicdn.com/tps/TB1G8PcHFXXXXarapXXXXXXXXXX-236-110.gif", Store.YES));
		doc.add(new IntField("status", 1, Store.YES));
		
		//创建文件系统的位置
		Directory dir = FSDirectory.open(new File("index"));
		 //定义分词器（标准分词器）
		Analyzer analyzer = new StandardAnalyzer();
		//索引写入对象的配置
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
		
		//先删除原有索引，再写入，默认OpenMode.APPEND
		indexWriterConfig.setOpenMode(OpenMode.CREATE);
		
		//索引写入对象
		IndexWriter writer = new IndexWriter(dir,indexWriterConfig);
		
		//写入
		writer.addDocument(doc);
		
		writer.close();
	}
	
	/**
	 * 创建中文分词索引库
	 * @throws Exception
	 */
	@Test
	public void testAddIndexIK() throws Exception {
		//创建文档，商品数据
		Document doc = new Document();
		doc.add(new LongField("id", 12, Store.YES));
		//TextField:做索引并分词
		doc.add(new TextField("title", "我爱爪哇 Apple/苹果 iPhone 6 Plus 5.5屏 移动4G联通电信4G 16G 银白", Store.YES));
		doc.add(new StringField("image", "https://img.alicdn.com/tps/TB1G8PcHFXXXXarapXXXXXXXXXX-236-110.gif", Store.YES));
		doc.add(new IntField("status", 1, Store.YES));
		
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
		writer.addDocument(doc);
		
		writer.close();
	}
	
	/**
	 * 标准词搜索
	 * @throws Exception
	 */
	@Test
	public void testSearch() throws Exception {
		//索引文件的位置
		Directory dir = FSDirectory.open(new File("index"));
		IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
		
		//词条搜索，设置关键字
		Query query = new TermQuery(new Term("title", "我爱爪哇"));
		
		//执行搜索，返回命中的数据
		TopDocs topDocs = indexSearcher.search(query, 10);
		
		System.out.println("命中数据总数：" + topDocs.totalHits);
		
		//遍历结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("得分：" + scoreDoc.score);
			//获得稳定Id
			Integer docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			
			System.out.println("Id：" + doc.get("id"));
			System.out.println("title：" + doc.get("title"));
			System.out.println("image" + doc.get("image"));
			System.out.println("status：" + doc.get("status"));
			
		}
	}
	
	/**
	 * 查询分词器搜索
	 * @throws Exception
	 */
	@Test
	public void testSearchAnalyzer() throws Exception {
		//索引文件的位置
		Directory dir = FSDirectory.open(new File("index"));
		IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
		
		//按分词器搜索，在搜索之前，进行关键字分词，再搜索
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("title", analyzer);
		Query query = parser.parse("苹果");
		
		//执行搜索，返回命中的数据
		TopDocs topDocs = indexSearcher.search(query, 10);
		
		System.out.println("命中数据总数：" + topDocs.totalHits);
		
		//遍历结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("得分：" + scoreDoc.score);
			//获得稳定Id
			Integer docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			
			System.out.println("Id：" + doc.get("id"));
			System.out.println("title：" + doc.get("title"));
			System.out.println("image" + doc.get("image"));
			System.out.println("status：" + doc.get("status"));
			
		}
	}
	
	
}
