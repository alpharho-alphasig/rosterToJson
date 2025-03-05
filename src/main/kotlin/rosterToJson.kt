import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import kotlin.system.exitProcess


val objectMapper: ObjectMapper = ObjectMapper()
    .configure(SerializationFeature.INDENT_OUTPUT, true)
    .registerKotlinModule()
    .findAndRegisterModules()

val illegalRosters = setOf("823")

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Expected filepath")
        exitProcess(1)
    }

    val workbook = WorkbookFactory.create(File(args.first()))
    val worksheet = workbook.getSheet("Rosters 350+")
    var i = 0
    var row = worksheet.getRow(i)
    formulaEvaluator = XSSFFormulaEvaluator(workbook as XSSFWorkbook)
    while (row.getCell(0).readString() != "351") {
        i += 1
        row = worksheet.getRow(i)
    }
    val brotherInfoList = mutableListOf<BrotherInfo>()
    while (true) {
        if (row.getCell(0).readString().isNotBlank())
            brotherInfoList.add(extractInfoFromRow(row))
        if (worksheet.getRow(i + 1).getCell(0).readString().isBlank() &&
            worksheet.getRow(i + 2).getCell(0).readString().isBlank()) {
            break
        }
        i += 1
        row = worksheet.getRow(i)
    }
    println(objectMapper.writeValueAsString(brotherInfoList.filter { it.rosterNumber !in illegalRosters }))
}

data class BrotherInfo(
    val rosterNumber: String,
    val lastName: String,
    val firstName: String,
    val pledgeClass: String,
    val crossingDate: String,
    val bigBrother: String,
    val nickName: String,
    val major: String
)

private fun getOrdinal(day: Int): String {
    if (day in 11..13) {
        return "th"
    }
    return when(day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

private val dataFormatter = DataFormatter()
val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
lateinit var formulaEvaluator: FormulaEvaluator
fun Cell.readString(): String {
    if (this.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(this)) {
        val date = this.dateCellValue
        return "${months[date.month]} ${date.date}${getOrdinal(date.date)}, ${date.year + 1900}"
    }
    return dataFormatter.formatCellValue(this, formulaEvaluator)
}

fun extractInfoFromRow(row: Row): BrotherInfo {
    return BrotherInfo(
        row.getCell(0).readString().trim(),
        row.getCell(1).readString().trim(),
        row.getCell(2).readString().trim(),
        row.getCell(3).readString().trim(),
        row.getCell(4).readString().trim(),
        row.getCell(5).readString().trim(),
        row.getCell(6).readString().trim(),
        row.getCell(7).readString().trim(),
    )
}
