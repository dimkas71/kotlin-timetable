package ua.compservice

import com.beust.jcommander.JCommander
import ua.compservice.command.HelpCommand
import ua.compservice.exception.TimetableException


object TimetableApplication {
    @JvmStatic
    fun main(args: Array<String>) {


        val helpCommand = HelpCommand()

        val commander = JCommander.newBuilder()
                .addObject(this)
                .addCommand(helpCommand)
                .args(args)
                .build()

        val command = commander.parsedCommand

        when (command) {
            "help" -> commander.usage()
            else -> {
                commander.usage()
            }
        }


    }
}