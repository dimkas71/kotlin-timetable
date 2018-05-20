package ua.compservice

import com.beust.jcommander.JCommander
import ua.compservice.command.HelpCommand
import ua.compservice.command.MergeSheetsCommand
import ua.compservice.util.loggerFor

val LOG = loggerFor<TimetableApplication>()

object TimetableApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        val helpCommand = HelpCommand()

        val mergeSheetsCommand = MergeSheetsCommand()

        val commander = JCommander.newBuilder()
                .addObject(this)
                .addCommand(helpCommand)
                .addCommand(mergeSheetsCommand)
                .args(args)
                .build()

        val command = commander.parsedCommand

        when (command) {
            "help" -> commander.usage()
            "merge-sheets" -> {
                mergeSheetsCommand.run {
                    LOG.debug("{}", mergeSheetsCommand)
                    mergeSheetsCommand.mergeSheets()
                }
            }
            else -> {
                commander.usage()
            }
        }

    }
}
