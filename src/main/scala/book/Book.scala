package book

import xml.XmlLoader
import zip.Zip
import java.io.File
import utils.Commands
import scala.io.Source
import scala.io.Codec
import java.nio.charset.CodingErrorAction.REPLACE
import scala.xml.dtd.EMPTY
import breeze.data.Text
import scala.util.Sorting
import breeze.text.segment.JavaSentenceSegmenter
import breeze.text.tokenize.PTBTokenizer
		

object Book{
	//Book 
	val bookURL = "http://nltk.googlecode.com/svn/trunk/nltk_data/packages/corpora/gutenberg.zip"
			val bookBaseDirName = "texts"
			val bookFilesName="gutenberg"
			val bookZipFileName = bookFilesName+".zip"

			def download={



			val bookDir = new File(bookBaseDirName)
			//If not exists "texts
			if(!bookDir.exists()){
				bookDir.mkdir
			}
			if(!new File(bookBaseDirName+"/"+bookZipFileName).exists()){
				println("Downloading book files...")			
				Zip.zipDownload(bookURL,bookFilesName,bookZipFileName)
			}

			if(!new File(bookBaseDirName+"/"+bookFilesName).exists()){
				Zip.unZip(bookZipFileName,bookFilesName)
				println("Unzip in "+bookDir.getAbsolutePath())
			}
	}
	def getTexts():List[Text]={
			implicit def codec = Codec("SJIS").onUnmappableCharacter(REPLACE)
					//Get file names in specified folder
					val list = Commands.ls(bookBaseDirName+"/"+bookFilesName)
					val bufList = scala.collection.mutable.ListBuffer[Text]()
					val sb = new StringBuilder
							
					list.foreach{file=>
							val src = Source.fromFile(file)
							val lines = src.getLines()
							
							//Fist line is name
							val name = lines.next()
							
							//Load texts after second line
							lines.foreach(line=>sb.append(line+" "))
							
							//Add list
							bufList+= new Text(name,sb.toString())
							sb.clear()
			}
			val texts = bufList.toList
					for(i <- 0 to texts.size-1){
						println("text"+i+": "+texts(i).id)
					}

			texts
	}

	def concordance(text:String,target:String)={
	  //Ignoring upper and lower case letters,
	  //and permit characters not being numbers and alphabets
		val r =  ("""\b(?i)"""+target+"""\b""").r	
				r.findAllIn(text).matchData.foreach{ m=>  
				println(text.substring(m.start-35, m.end+35).replace('\n',' '))
		}
	}

	def similar(text:String,target:String)={
	  
		//Tokenize
		val sentences = (new JavaSentenceSegmenter)(text).toIndexedSeq
		val tokenized = sentences.map(PTBTokenizer())
		val bufSet = scala.collection.mutable.HashSet[(String,String)]()
		
		//Ignoring upper and lower case letters 
		val r = ("""(?i)"""+target).r

		//Create a pair words list
		//w0 w1 w2 >> (w1,w3)
		tokenized foreach{t =>
			val list = t.toList
			for(i <- 1 to (list.size -2)){
				if(r.findFirstIn(list(i))!=None)
					bufSet+=((list(i-1),list(i+1)))
			}
		}
		
		//Find words resembling target
		//w1' resemble w1. (w0 w1 w2) and (w0 w1' w2)
		val similarWord = scala.collection.mutable.HashMap[String,Int]()
		val pre_post = bufSet.toList
		tokenized foreach{ t =>
			val list = t.toList
			for(i <- 1 to (list.size-2)){
				pre_post foreach{
				case (pre,post) =>{
					if(pre==list(i-1)&&post==list(i+1)&&r.findFirstIn(list(i))==None){

					  //Fist time value is 1
						if(similarWord.contains(list(i)))
							similarWord+=(list(i) -> (similarWord(list(i))+1))
						else
							similarWord+=(list(i) -> 1)
						
					}
				}
				}
			}
		}
		//Sorting second variable
		val result = Sorting.stableSort(
		    similarWord.toSeq,
		    (a:(String,Int),b:(String,Int))=> a._2 > b._2)
		for(i <- 0 to result.length-1 )println(result(i))
	}


	def common_contexts(text:String,targets:String*)={
		val sentences = (new JavaSentenceSegmenter)(text).toIndexedSeq
		var tokenized = sentences.map(PTBTokenizer())
		var result = Set[(String,String)]()

		for(target <- targets){
		  	//Create a pair words list
			//w1 w2 w3 >> (w1,w3)
			val bufSet = scala.collection.mutable.Set[(String,String)]()
					val r = ("""(?i)"""+target).r
					tokenized foreach{
				t=>
				val list = t.toList
				for(i <- 1 to (list.size -2)){
					if(r.findFirstIn(list(i))!=None)
						bufSet+=((list(i-1),list(i+1)))
				}
			}
			if(result.isEmpty){
				result=bufSet.toSet
			}else{
				//Leave match elements
				result = result & bufSet.toSet
			}
		}
		println(result)
	}
	
}