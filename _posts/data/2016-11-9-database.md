---
layout: post
title: Android数据库并发操作
category: 数据处理
keywords: Android，sqlite
---


>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

# 1.Android SQLite函数调用

首先在SQLiteDatabase中有已经封装好的增删改查操作函数，操作简单数据自己在封装一个dao直接调用即可,操作复杂可以自定义SQL语句，但是里面都是通过SQLiteStatement来执行其对应操作。查询操作执行和其他不同，是通过SQLiteCursorDriver来找到curser的。

	查询(读):外层包装->SQL语句->SQLiteCursorDriver
	增删改(写)：外层包装->SQL语句->SQLiteStatement
	

所以可以直接如网上所说，用来SQLiteStatement写，将

	SQLiteDatabase database = new SQLiteDatabase();  
	if (database.isOpen())   
	{  
	    database.beginTransaction();  
	    try {  
	         //sql为insert into tableName (name) values ("test")  
	        database.execSQL(sql);  
	    }  
	        database.setTransactionSuccessful();  
	    } finally {  
	        database.endTransaction();  
	    }  
	    database.close();  
	} 

改为

	SQLiteDatabase database = new SQLiteDatabase();  
	//sql为insert into tableName (name) values (?)  
	SQLiteStatement sqlListStatment = database.compileStatement(sql);  
	if (database.isOpen())   
	{  
	    database.beginTransaction();  
	    try {  
	        //index 为1开始索引，value为入库的值  
	        //bingXXX为插入XXX类型
	         sqLiteStatement.bindString(index, value);  
	         sqLiteStatement.executeInsert();  
	    }  
	        database.setTransactionSuccessful();  
	    } finally {  
	        database.endTransaction();  
	    }  
	    database.close();
	}

这样性能应该会提升不少，因为SQLiteDatabase封装的时候做了一些检查操作，每一条省下的时间可能微不足道，但如果数据过万就不同了。


# 2.事务操作

Android SQL语句默认一条语句是一个事务，事务是为了保证操作的安全性，但是事务实际执行却会耗费大量资源，如果反复执行一个插入操作10000次将会相当耗时，所以在反复执行增删改操所时，一定在外层加入事务，让整个任务作为一条事务来执行。

		db.beginTransaction();
		try{
			//批量写操作
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}


# 3.SQLite database is locked

这个是由于同时操作数据库抛出的异常，数据库同一时间不能被并发访问，这一点用单例模式可以解决，SQLiteOpenHelper在多个线程创建多个对象不能同时读和写，只能同时读，但是用单例模式，单个SQLiteOpenHelper对象在不同线程可以同时读写。

关于SQLiteOpenHelper用单例在不同线程同时执行读写，写写操作，我做了一些测试，发现数据库的访问是顺序执行的，两个线程同时进行写操作，先执行的会先操作，另一个线程就阻塞了，等到先执行的操作完成，另一个线程才开始操作。

