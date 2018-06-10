package ua.compservice

import com.beust.jcommander.JCommander
import ua.compservice.command.CheckPersonnelNumberCommand
import ua.compservice.command.HelpCommand
import ua.compservice.command.MergeCommand
import ua.compservice.command.MergeSheetsCommand
import ua.compservice.util.loggerFor

val LOG = loggerFor<TimetableApplication>()

object TimetableApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        val helpCommand = HelpCommand()
        val mergeCommamnd = MergeCommand()

        val mergeSheetsCommand = MergeSheetsCommand()

        val checkPersonnelNumberCommand = CheckPersonnelNumberCommand()

        val commander = JCommander.newBuilder()
                .addObject(this)
                .addCommand(helpCommand)
                .addCommand(mergeCommamnd)
                .addCommand(mergeSheetsCommand)
                .addCommand(checkPersonnelNumberCommand)
                .args(args)
                .build()

        val command = commander.parsedCommand

        when (command) {
            "help" -> commander.usage()
            "merge" -> {
                mergeCommamnd.run {
                    LOG.debug("{}", mergeCommamnd)
                    mergeCommamnd.merge()
                }

            }
            "merge-sheets" -> {
                mergeSheetsCommand.run {
                    LOG.debug("{}", mergeSheetsCommand)
                    mergeSheetsCommand.mergeSheets()
                }
            }
            "check-pn" -> {
                LOG.debug("{}", checkPersonnelNumberCommand)
                checkPersonnelNumberCommand.check()
            }
            else -> {
                commander.usage()
            }
        }

    }
}
