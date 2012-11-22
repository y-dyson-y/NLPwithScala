package zip
import scala.xml.Elem

object Zip {

  /**
   * List up package in xml
   */
  def getZipList(xml:Elem):List[(String,String)]={
	//Pick up package information
    val packages = (xml \ "packages") \ "package"
    
    //Add name and zip URL of book in package information
      var list = List.empty[(String,String)]
    		  for(pack <- packages){
    		    //Get attributes
    		      val name = (pack \ "@name").toString()
    		      val zipUrl = (pack \ "@url").toString()
    		      list = (name,zipUrl)::list
    		  }
    return list.reverse
  }
  
  /**
	 * Download zip file from specified URL 
	 */
	def zipDownload(zipUrl:String,directory:String,filename:String)={
	  import java.net.URL
	  import java.io.{File,FileOutputStream}
	  
	  val url = new URL(zipUrl)
	  val conn = url.openConnection()
	  val in = conn.getInputStream()
	  val out = new FileOutputStream(new File(directory,filename),false)
	  
	  var b = -1
	  while({b=in.read(); b != -1}){out.write(b)}
	  in.close()
	  out.close()
	}
	
	/**
	 * Unzip
	 */
	def unZip(zipFilename:String,dirName:String)={
	  import java.util.zip.{ZipEntry, ZipFile}
	  import java.io.{File,FileOutputStream}
	  import java.io.{BufferedInputStream,BufferedOutputStream}

			  
	  val file = new File(dirName,zipFilename)
	  
	  //Get name to create an expansion directory
	  val exindex = zipFilename.lastIndexOf(".")
	  val baseDirName = zipFilename.substring(0,exindex)	  
	  
	  //Create an expansion directory
	  val baseDir = new File(file.getParent(), baseDirName)
	  baseDir.mkdir

	  //Unzip
	  val zipFile = new ZipFile(file)
	  val entries = zipFile.entries
	  while(entries.hasMoreElements){
		  val ze = entries.nextElement
		  
		  //Remove folderName from ze name
		  //ex. gutenberg/whitman.txt >> whitman
		  val zename = ze.getName()
		  val zeFilename = zename.substring(zename.lastIndexOf("/")+1)
		  val outFile = new File(baseDir,zeFilename)

		  //element is directory
		  if(ze.isDirectory){
		    //Create directory
			  outFile.mkdirs
		  }else{
			  val is = zipFile.getInputStream(ze)
			  val bis = new BufferedInputStream(is)
			  val bos = new BufferedOutputStream(new FileOutputStream(outFile))
			  
			  var b = -1
			  while({b = bis.read; b > 0}){
				  bos.write(b)
			  }
			  bis.close
			  bos.close
		  }
	  }
	}
}