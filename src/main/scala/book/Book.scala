package book

import xml.XmlLoader
import zip.Zip
import java.io.File
import utils.Commands
import scala.io.Source
import scala.io.Codec
import java.nio.charset.CodingErrorAction.REPLACE
import scala.xml.dtd.EMPTY

object Book{
	//Book 
	val bookURL = "http://nltk.googlecode.com/svn/trunk/nltk_data/packages/corpora/gutenberg.zip"
	val bookBaseDirName = "texts"
	val bookDirName="gutenberg"
	val bookZipFileName = bookDirName+".zip"

	  def download:List[String]={
			implicit def codec = Codec("SJIS").onUnmappableCharacter(REPLACE)
   
	  /*
	    println("Downloading book files...")
	   
	   val bookDir = new File(bookDirName)
	   bookDir.mkdir
	  	Zip.zipDownload(bookURL,bookDirName,bookZipFileName)
	  	
	  	Zip.unZip(bookZipFileName,bookDirName)
	  	println("Donloaded in "+bookDir.getAbsolutePath())
*/
	  	val list = Commands.ls(bookBaseDirName+"/"+bookDirName)
	  	var texts = List.empty[String]
		var names = List.empty[String]
	  	  list.foreach{file=>
	  	  val src = Source.fromFile(file)
	  	  val lines = src.getLines()
	  	  names = lines.next()::names
	  	  val sb = new StringBuilder
	  	  lines.foreach(sb.append)
	  	  texts=sb.toString()::texts
	  	  }
		texts = texts.reverse
		names = names.reverse
		for(i <- 0 to texts.size-1){
		  println("text"+i+": "+names(i))
		}
		
		texts
/*	  	
	    val xml = XmlLoader.load(bookURL)
	    val list = Zip.getZipList(xml)
	    
	    println("************ BOOK LIST **************")
	    list.zipWithIndex foreach{case(pack,i)=>printf("%2d: %s\n",i,pack._1)}
	  	
	  	println("\nEnter book number (0-"+(list.size-1)+"):")

	  	val bookNumStr = readLine()
	  	val bookNum = bookNumStr.toInt
	  	val zipFileName = "Book"+list(bookNum)._1+".zip"
	  	val zipURL = list(bookNum)._2
	  	
	
	*/
	}
	
	
}