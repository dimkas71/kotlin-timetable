package ua.compservice

import com.beust.jcommander.JCommander
import ua.compservice.command.HelpCommand
import ua.compservice.command.MergeSheetsCommand
import ua.compservice.exception.TimetableException
import ua.compservice.util.loggerFor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


val LOG = loggerFor<TimetableApplication>()
val HOME_DIR = System.getProperty("user.dir").toString()

fun mergeSheets(from: Path, to: Path, withTeam: Boolean = false) {



}

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

                    val homePath = Paths.get(HOME_DIR)
                    val from = homePath.resolve(mergeSheetsCommand.input)

                    if (!Files.exists(from)) {
                        val message = "Merging sheets command: File $from does'nt exist"
                        LOG.error("{}", message)
                        throw TimetableException(message)
                    }
                    val to = homePath.resolve(mergeSheetsCommand.output)
                    mergeSheets(from, to, mergeSheetsCommand.withTeam)
                }
            }
            else -> {
                commander.usage()
            }
        }



    }
}
