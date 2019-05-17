package log5j.spi;

import log5j.Category;
import log5j.Priority;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class PocDeserialization {

    public static void main(String[] args) throws Exception {

        LoggingEvent ev = new LoggingEvent("", Category.getRoot(), Priority.INFO, "test",null);

        //Serializing the payload
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buffer);
        out.writeObject(ev);

        //Saving to file
        File destination = new File("serial-payload");
        try(OutputStream file = new FileOutputStream(destination)) {
            file.write(buffer.toByteArray());
            file.flush();
        }
        System.out.println("File written to "+destination.getCanonicalPath());

        //Preview raw
        System.out.println(buffer);

        //Test the payload
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        Object obj = in.readObject();
    }
}
