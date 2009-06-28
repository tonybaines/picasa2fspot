WORD=/\w_\.\-/
IMAGE=/\[([$WORD]+jpg)\]/
STARRED=/star\=yes/
STARRED_IMAGES=/(?is:$IMAGE([\s|$WORD=$WORD])+?$STARRED)/

def markAsFavourites(dir) {
	new File("${dir}/Picasa.ini").text.eachMatch(STARRED_IMAGES) {
		def filename = "${dir}/${it[1]}"
		def exifSubjects = getExifSubjects(filename)
		println "Marking ${filename} as a Favourite"
		addExifSubject('Favourites', filename)
	}
}

def getExifSubjects(filename) {
	"exiftool -Subject ${filename}".execute().text
		.split(/:/)[1].trim().split(/\s*,\s*/)
}

def addExifSubject(subject, filename) {
	List subjects = getExifSubjects(filename)
	subjects += subject
	def uniqueNonEmptySubjects = subjects.unique().findAll {!it.trim().empty}
	"exiftool -overwrite_original -P -Subject=${uniqueNonEmptySubjects.join(',')} ${filename}".execute()	
}


def cli = new CliBuilder(usage: 'Specify the directory to process')
cli.h(longOpt: 'help', 'Usage')
cli.d(longOpt: 'dir', args: 1, required: true, 'Directory to process')

def options = cli.parse(args)
if (!options) return
if (options.h) cli.usage()

markAsFavourites options.d

