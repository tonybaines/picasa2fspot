def String text = new File('Winter/Picasa.ini').text
WORD=/\w_\.\-/
IMAGE=/\[([$WORD]+jpg)\]/
STARRED=/star\=yes/
STARRED_IMAGES=/(?is:$IMAGE([\s|$WORD=$WORD])+?$STARRED)/

text.eachMatch(STARRED_IMAGES) {
	def filename = "Winter/${it[1]}"
	def exifSubjects = getExifSubjects(filename)
	println "Marking ${filename} as a Favourite"
	addExifSubject('Favourites', filename)
}

def getExifSubjects(filename) {
	"exiftool -Subject ${filename}".execute().text
		.split(/:/)[1].trim().split(/\s*,\s*/)
}

def addExifSubject(subject, filename) {
	List subjects = getExifSubjects(filename)
	subjects += subject
	def uniqueNonEmptySubjects = subjects.unique().findAll {!it.trim().empty}
	"exiftool -overwrite_original -Subject=${uniqueNonEmptySubjects.join(',')} ${filename}".execute()	
}