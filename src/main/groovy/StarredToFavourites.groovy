import java.io.FileNotFoundExceptionWORD=/\w_\.\-/
IMAGE=/\[([$WORD]+jpg)\]/
STARRED=/star\=yes/
STARRED_IMAGES=/(?is:$IMAGE([\s|$WORD=$WORD])+?$STARRED)/

def markAsFavourites(dir) {	println "Processing images ${dir}"
	new File("${dir}/Picasa.ini").text.eachMatch(STARRED_IMAGES) {
		def filename = "${dir}/${it[1]}"
		def exifSubjects = getExifSubjects(filename)
		println "Marking ${filename} as a Favourite"
		addExifSubject('Favourites', filename)
	}
}

def getExifSubjects(filename) {
	def subjects = "exiftool -Subject ${filename}".execute().text
	subjects.contains(':')? subjects.split(/:/)[1].trim().replaceAll(/[\'\"]/, '').split(/\s*,\s*/) : []}

def addExifSubject(subject, filename) {
	List subjects = getExifSubjects(filename)	if (subjects.contains(subject)) {		println "${filename} is already tagged as ${subject}"	}	else {		"exiftool -overwrite_original -P -Subject+=${subject} ${filename}".execute()		}
	
//	subjects += subject
//	def uniqueNonEmptySubjects = subjects.unique().findAll {!it.trim().empty}//	"exiftool -overwrite_original -P -Subject=${uniqueNonEmptySubjects.join(',')} ${filename}".execute()	}def processDir(base) {    base.eachDirRecurse {		println "Checking for Picasa metadata in ${it.absolutePath}"    	if (new File(it.absolutePath + '/Picasa.ini').exists()) {    		markAsFavourites it    	}    }}

def cli = new CliBuilder(usage: 'Specify the directory to process')
cli.h(longOpt: 'help', 'Usage')
cli.d(longOpt: 'dir', args: 1, required: true, 'Directory to process')
cli.r(longOpt: 'recursive', required: false, 'Find all sub-directories with Picasa metadata and process them')

def options = cli.parse(args)
if (!options) return
if (options.h) cli.usage()
if (options.r) {	processDir(new File(options.d))}else {
	try {
		markAsFavourites options.d
	}
	catch (FileNotFoundException e) {		System.err.println("${options.d} doesn't have the Picasa.ini metadata file")	}
}
