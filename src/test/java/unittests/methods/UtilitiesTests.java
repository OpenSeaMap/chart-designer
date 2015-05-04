/*******************************************************************************
 * Copyright (c) OSMCB developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package unittests.methods;

import osmcd.OSMCBUtilities.OSMCBUtilities;
import junit.framework.TestCase;
import junit.textui.TestRunner;

public class OSMCBUtilitiesTests extends TestCase {

	public void testParseSVNRevision() {
		assertEquals(4168, OSMCBUtilities.parseSVNRevision("4168"));
		assertEquals(4168, OSMCBUtilities.parseSVNRevision("4123:4168"));
		assertEquals(4168, OSMCBUtilities.parseSVNRevision("4168M"));
		assertEquals(4168, OSMCBUtilities.parseSVNRevision("4212:4168MS"));
		assertEquals(4168, OSMCBUtilities.parseSVNRevision("$Revision:	4168$"));
		assertEquals(4168, OSMCBUtilities.parseSVNRevision("$Rev: 4212:4168MS$"));
		assertEquals(-1, OSMCBUtilities.parseSVNRevision("exported"));
	}

	public static void main(String[] args) {
		TestRunner.run(OSMCBUtilitiesTests.class);
	}

}
