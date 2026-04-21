package tests;

import org.junit.jupiter.api.Test;

import DomainLayer.Role;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Role domain class.
 * Covers construction validation and equality semantics.
 */
public class RoleTest {

    // ── Test 11 ─────────────────────────────────────────────────────────────
    /** A roleId of 0 is explicitly forbidden by the domain rules. */
    @Test
    void constructor_zeroRoleId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Role(0, "Cashier", "Checkout and customers"));
    }

    // ── Test 12 ─────────────────────────────────────────────────────────────
    /** Two Role objects with the same ID must be considered equal. */
    @Test
    void equals_sameRoleId_returnsTrue() {
        Role r1 = new Role(Role.CASHIER_ID, "Cashier", "Desc A");
        Role r2 = new Role(Role.CASHIER_ID, "CashierAlias", "Desc B");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    // ── Test 13 ─────────────────────────────────────────────────────────────
    /** Two Role objects with different IDs must not be equal. */
    @Test
    void equals_differentRoleId_returnsFalse() {
        Role r1 = new Role(Role.CASHIER_ID, "Cashier", "Desc");
        Role r2 = new Role(Role.STOREKEEPER_ID, "Storekeeper", "Desc");
        assertNotEquals(r1, r2);
    }
}
