
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


/**

*/
public class AtcGameVersion
{

	private final int majorVersion;
	private final int minorVersion;
	private final int patchVersion;
	private final String prereleaseVersion;


	/** @param majorVersion
	 * /** @param minorVersion
	 * /** @param patchVersion
	 * /** @param prereleaseVersion */
	public AtcGameVersion(
			int majorVersion, int minorVersion, int patchVersion,
			String prereleaseVersion
	) {
		super();
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = patchVersion;
		this.prereleaseVersion = prereleaseVersion;
	}


	public int getMajorVersion(
	) {
		return majorVersion;
	}


	public int getMinorVersion(
	) {
		return minorVersion;
	}


	public int getPatchVersion(
	) {
		return patchVersion;
	}


	public String getPrereleaseVersion(
	) {
		return prereleaseVersion;
	}


	@Override
	public String toString(
	) {
		return "GameVersion v" + majorVersion + "." + minorVersion
				+ "." + patchVersion
				+ (prereleaseVersion.isEmpty() ? "" : ("-" + prereleaseVersion));
	}

}
