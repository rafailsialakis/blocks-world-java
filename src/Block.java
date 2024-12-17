import java.util.Objects;

public class Block {
    private String name;

    public Block(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    // Override equals() and hashCode() to compare blocks by their name
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Block block = (Block) obj;
        return name.equals(block.name); // Compare based on name or other unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); // Ensure hashCode is consistent with equals
    }

    @Override
    public String toString() {
        return name; // For easier printing
    }
}
