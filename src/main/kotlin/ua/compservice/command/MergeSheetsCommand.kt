package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters

val DEFAULT_OUT = "output.xlsx"

@Parameters(commandNames = ["merge-sheets"], commandDescription = "Merge sheets from an input file to the output file")
data class MergeSheetsCommand(
        @Parameter(names = ["-f", "--file"], description = "input file", required = true)
        var input:String = "",

        @Parameter(names = ["-wt", "--with-team"], description = "if used then column TEAM will be inserted" +
                "before the first timesheet's day(1). The team will get from a sheet's name")
        var withTeam: Boolean = false,

        @Parameter(names = ["-o", "--output"], description = "Output file", required = false)
        var output: String = DEFAULT_OUT )