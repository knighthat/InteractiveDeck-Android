package me.knighthat.interactivedeck.connection.wireless

import me.knighthat.interactivedeck.connection.request.Request
import me.knighthat.interactivedeck.console.Log
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class WirelessSender : Thread() {

    companion object {
        private val QUEUE: BlockingQueue<Request> = LinkedBlockingQueue(20)
        private lateinit var stream: OutputStream

        @JvmStatic
        @Throws(InterruptedException::class)
        fun send(request: Request) {
            QUEUE.put(request)
        }

        fun start(stream: OutputStream) {
            QUEUE.clear()
            Companion.stream = stream
            WirelessSender().start()
        }
    }


    init {
        name = "NET/O"
    }

    override fun run() {
        var request: Request
        while (!interrupted()) {
            try {
                request = QUEUE.take()
                val serialized = request.serialize()

                Log.deb("Sending:")
                Log.deb(serialized)

                stream.write(serialized.toByteArray())
                stream.flush()
            } catch (e: InterruptedException) {
                //TODO Needs proper error handling
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}