fun isValidIdentifier(s: String): Boolean {
    if (s.isEmpty()) return false
    
    // Check for first char
    if (!((s[0] in 'a'..'z') || (s[0] in 'A'..'Z') || (s[0].toInt() == 95))) {
        //println("First char have issue")
        return false
    }
    
    // Check for invalid char.
    for (char in s){
        if (!((char in 'a'..'z') || (char in 'A'..'Z') || (char in '0'..'9') || (char.toInt() == 95))) {
            //println("Invalid char have issue")
            return false
        }
    }
    return true
}

fun main() {
    println(isValidIdentifier("name"))   // true
    println(isValidIdentifier("_name"))  // true
    println(isValidIdentifier("_12"))    // true
    println(isValidIdentifier(""))       // false
    println(isValidIdentifier("012"))    // false
    println(isValidIdentifier("no$"))    // false
}
