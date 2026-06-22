package DomainLayer;

/**
 * Domain entity representing a single branch of the chain.
 * <p>
 * Branches are owned and registered by {@link EmployeeController}. A branch is
 * identified solely by its id; that is the only information the existing code
 * uses, so it is the only field this class carries. {@link #equals(Object)} and
 * {@link #hashCode()} are defined by id so a branch can be looked up in the
 * controller's collection.
 */
public class Branch {
    private final int id;

    public Branch(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Branch)) {
            return false;
        }
        return id == ((Branch) obj).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
