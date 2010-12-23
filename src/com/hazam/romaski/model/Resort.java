package com.hazam.romaski.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class Resort extends ArrayList<Uri> {
	
	
	public static Map<String, Resort> ALL = new HashMap<String,Resort>();
	public static Resort CAMPO_FELICE = new Resort("cfelice");
	public static Resort OVINDOLI = new Resort("ovindoli");
	public static Resort GRAN_SASSO = new Resort("gransasso");
	
	static {
		CAMPO_FELICE.setName("Campo Felice");
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/01/images/campofelice_zoom_fpdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/05/images/campofelice05_pdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/02/images/campofelice02_pdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/02/images/campofelice02_zoom_gpdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/03/images/campofelice03_pdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/04/images/campofelice_pdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/04/images/campofelice_pdah.jpg"));
		OVINDOLI.setName("Ovindoli");			
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/002/images/ovindolimagnola_pda480.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/002/images/ovindolimagnola_zoom_hpda480.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/003/images/ovindolimagnola_pdah.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/004/images/ovindolimagnola_resize_pda480.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/004/images/ovindolimagnola_zoom_fpda480.jpg"));
		GRAN_SASSO.setName("Gran Sasso");
		GRAN_SASSO.add(Uri.parse("http://www.meteoappennino.it/webgransasso/01/images/campoimperatore_pdah.jpg"));		
		ALL.put(CAMPO_FELICE.getId(), CAMPO_FELICE);
		ALL.put(OVINDOLI.getId(), OVINDOLI);
		ALL.put(GRAN_SASSO.getId(), GRAN_SASSO);
	}
	
	private String id = null;
	private String name = null;
	
	public Resort(String _id) {
		this.id = _id;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
