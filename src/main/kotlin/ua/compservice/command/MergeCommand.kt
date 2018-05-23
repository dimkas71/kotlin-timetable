package ua.compservice.command

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import ua.compservice.util.loggerFor


@Parameters(commandNames = ["merge"], commandDescription = "Merge xlsx files into an one output file")
data class MergeCommand(
        @Parameter(description = "list of files to merge", required = true)
        var files: List<String> = mutableListOf(),

        @Parameter(names = ["-wt", "--with-team"], description = "if used then column TEAM will be inserted before the first timesheet's day(1)",
                required = false)
        var witTeam: Boolean = false,

        @Parameter(names = ["-o", "--output"], description = "Optional output file, by default equals to output.xlsx", required = false)
        var output: String = DEFAULT_OUT

) {
    fun merge() {
        LOG.debug("{}", this)
    }

    companion object {
        val LOG = loggerFor<MergeCommand>()
        val DEFAULT_OUT = "output.xlsx"
    }
}