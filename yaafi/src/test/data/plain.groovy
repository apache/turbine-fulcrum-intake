import java.io.File;

def getVerse() {
    lines= []
    lines+= ( "Karma " * 5 ) + "Chameleon,"
    lines+= (["you come and go"] * 2).join(", ")+"."
    lines+= "Loving would be easy if your"+
              " colours were like my dream"
    lines+= (["red gold and green"]*2).join(", ")+"."

    verse= lines.collect { it + "\n" }
    return verse
}

println "Writing out to file.\n"
myFile= new File("temp/karma.txt")
myFile.write("Karma Chameleon\n\n")
getVerse().each { myFile.append(it) }

println "\nWith line numbering ...\n"
count=1
myFile.eachLine { println count++ + ": " + it }

println "\nAs a single string ...\n"
println myFile.readLines().join(" | ")
myFile.delete();