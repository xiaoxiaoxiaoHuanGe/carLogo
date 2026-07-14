package com.example.carlogo.ui

import com.example.carlogo.data.SeedData
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BrandLogoRegistryTest {

    @Test
    fun `every built in brand resolves to a bundled logo and custom brands do not`() {
        val missingBrandIds = SeedData.brands
            .map { it.id }
            .filter { BrandLogoRegistry.resourceIdFor(it) == null }

        assertTrue("Missing logo mappings: $missingBrandIds", missingBrandIds.isEmpty())
        assertNull(BrandLogoRegistry.resourceIdFor("custom-user-brand"))
    }
}
