package utils
import java.io.File

object Commands {
	def ls(dir:String):Seq[File]={
			new File(dir).listFiles.flatMap{
			case f if f.isDirectory => ls(f.getPath)
			case x => List(x)
			}
	}
}