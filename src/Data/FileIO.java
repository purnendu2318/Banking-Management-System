package Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import Bank.Bank;

public final class FileIO {
    private static final String DATA_FILE = "data";
    private static final String TEMP_FILE = "data.tmp";
    public static Bank bank = new Bank();

    private FileIO() { }

    public static void Read() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Object saved = input.readObject();
            if (saved instanceof Bank) bank = (Bank) saved;
            else bank = new Bank();
        } catch (IOException | ClassNotFoundException ex) {
            Path dataPath = Path.of(DATA_FILE);
            if (Files.exists(dataPath)) {
                try {
                    Files.copy(dataPath, Path.of(DATA_FILE + ".backup"), StandardCopyOption.REPLACE_EXISTING);
                    System.err.println("Could not read banking data; a backup was saved to " + DATA_FILE + ".backup");
                } catch (IOException backupError) {
                    System.err.println("Could not read or back up banking data: " + backupError.getMessage());
                }
            }
            bank = new Bank();
        }
    }

    public static void Write() {
        Path target = Path.of(DATA_FILE);
        Path temp = Path.of(TEMP_FILE);
        try {
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temp))) {
                output.writeObject(bank);
                output.flush();
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            try { Files.deleteIfExists(temp); } catch (IOException ignored) { }
            throw new IllegalStateException("Could not save banking data: " + ex.getMessage(), ex);
        }
    }
}
