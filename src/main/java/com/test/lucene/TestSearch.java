package com.test.lucene;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class TestSearch{
	
	/**
	 * 词条搜索
	 */
	@Test
	public void testTermQuery() throws Throwable {
		Query query = new TermQuery(new Term("title", "苹果"));
		search(query);
	}
	
	/**
	 * 范围搜索
	 */
	@Test
	public void testNumericRangeQuery() throws Throwable {
		//最小值、最大值边界
		Query query = NumericRangeQuery.newLongRange("id", 20L , 40L, true, true);
		search(query);
	}
	
	/**
	 * 匹配全部
	 */
	@Test
	public void testMatchAllDocsQuery() throws Throwable {
		//最小值、最大值边界
		Query query = new MatchAllDocsQuery();
		search(query);
	}
	
	
	/**
	 * 模糊搜索
	 * ?代表1个任意字符
	 * *代表0或者多个任意字符
	 */
	@Test
	public void testWildcardQuery() throws Throwable {
		//最小值、最大值边界
		Query query = new WildcardQuery(new Term("title", "9*g"));
		search(query);
	}
	
	/**
	 * 相似度搜索
	 * 采用编辑距离算法实现
	 * 从一个字符串变成另一个字符串所需的最少变化操作步骤，
	 * 默认最大编辑距离为2
	 */
	@Test
	public void testFuzzyQuery() throws Throwable {
		Query query = new FuzzyQuery(new Term("title", "eaple"), 2);
		search(query);
	}
	
	/**
	 * 组合搜索
	 * MUST 必须包含
	 * MUST_NOT 不能包含
	 * SHOULD 或
	 */
	@Test
	public void testBooleanQeruy() throws Throwable {
		BooleanQuery booeanQuery = new BooleanQuery();
		
		//必须包含
		Query query = NumericRangeQuery.newLongRange("id", 80L , 100L, true, true);
		booeanQuery.add(query , Occur.MUST);
		
		Query query2 = new WildcardQuery(new Term("title", "9*g"));
		booeanQuery.add(query2 , Occur.MUST);
		
		//不能包含
		Query query3 = new TermQuery(new Term("title", "90g"));
		booeanQuery.add(query3 , Occur.MUST_NOT);
		
		search(booeanQuery);
	}
	
	
	public void search(Query query) throws Exception {
		//索引文件的位置
		Directory dir = FSDirectory.open(new File("index"));
		IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
		
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