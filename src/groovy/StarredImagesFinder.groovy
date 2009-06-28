def String text = new File('Winter/Picasa.ini').text
WORD=/\w_\.\-/
IMAGE=/\[([$WORD]+jpg)\]/
STARRED=/star\=yes/
STARRED_IMAGES=/(?is:$IMAGE([\s|$WORD=$WORD])+?$STARRED)/

text.eachMatch(STARRED_IMAGES) {
	def filename = "Winter/${it[1]}"
	def exifSubjects = getExifSubjects(filename)
	println "Subjects of ${filename}: ${exifSubjects}"
	addExifSubject('Favourites', filename)
	println "Subjects of ${filename}: ${getExifSubjects(filename)}"
}

def getExifSubjects(filename) {
	def command = "exiftool -Subject ${filename}"
	command.execute().text.split(/:/)[1].trim().split(/\s*,\s*/)
}

def addExifSubject(subject, filename) {
	List subjects = getExifSubjects(filename)
	subjects += subject
	def uniqueNonEmptySubjects = subjects.unique().findAll {!it.trim().empty}
	def command = "exiftool -Subject=${uniqueNonEmptySubjects.join(',')} ${filename}"
	println command
	command.execute()	
}