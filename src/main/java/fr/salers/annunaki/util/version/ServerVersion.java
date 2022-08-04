package fr.salers.annunaki.util.version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

@AllArgsConstructor
@Getter
public enum ServerVersion {

	UNSUPPORTED(0), EIGHT(8), NINE(9), TEN(10), ELEVEN(11), TWELVE(12), THIRTEEN(13), FOURTEEN(14), FIFTEEN(15), SIXTEEN(16), SEVENTEEN(17), EIGHTEEN(18), NINETEEN(19);

	final int version;

	public static ServerVersion get() {
		String ver = Bukkit.getVersion();

		// TODO: Simplify this there is a much more readable and easier way of doing it.
		return ver.contains("1.8") ? EIGHT : ver.contains("1.9") ? NINE : ver.contains("1.10") ? TEN : ver.contains("1.11") ? ELEVEN : ver.contains("1.12") ? TWELVE : ver.contains("1.13") ? THIRTEEN : ver.contains("1.14") ? FOURTEEN : ver.contains("1.15") ? FIFTEEN : ver.contains("1.16") ? SIXTEEN : ver.contains("1.17") ? SEVENTEEN : ver.contains("1.18") ? EIGHTEEN : ver.contains("1.19") ? NINETEEN : UNSUPPORTED;
	}
}
