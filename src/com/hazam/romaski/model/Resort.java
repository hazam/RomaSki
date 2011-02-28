package com.hazam.romaski.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.net.Uri;

import com.hazam.romaski.R;

public class Resort extends ArrayList<Uri> {
	private static final long serialVersionUID = 6668480187615423193L;
	public static Map<String, Resort> ALL = new LinkedHashMap<String,Resort>();
	public static Resort CAMPO_FELICE = new Resort("cfelice");
	public static Resort OVINDOLI = new Resort("ovindoli");
	public static Resort GRAN_SASSO = new Resort("gransasso");
	
	static {
		CAMPO_FELICE.setName("Campo Felice");
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/01/images/campofelice_zoom_fpdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/02/images/campofelice02_pdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/03/images/campofelice03_pdah.jpg"));
		CAMPO_FELICE.add(Uri.parse("http://www.meteoappennino.it/webcampofelice/04/images/campofelice_pdah.jpg"));
		CAMPO_FELICE.setDrawableId(R.drawable.felice);
		OVINDOLI.setName("Ovindoli");			
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/001/images/ovindolimagnola_zoom_gpdah.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/002/images/ovindolimagnola_pda480.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/002/images/ovindolimagnola_zoom_hpda480.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/003/images/ovindolimagnola_pdah.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/004/images/ovindolimagnola_resize_pda480.jpg"));
		OVINDOLI.add(Uri.parse("http://www.meteoappennino.it/webovindoli/004/images/ovindolimagnola_zoom_fpda480.jpg"));
		OVINDOLI.setDrawableId(R.drawable.ovindoli);
		GRAN_SASSO.setName("Gran Sasso");
		GRAN_SASSO.add(Uri.parse("http://www.meteoappennino.it/webgransasso/01/images/campoimperatore_pdah.jpg"));
		GRAN_SASSO.setDrawableId(R.drawable.gran_sasso);
		ALL.put(CAMPO_FELICE.getId(), CAMPO_FELICE);
		ALL.put(OVINDOLI.getId(), OVINDOLI);
		ALL.put(GRAN_SASSO.getId(), GRAN_SASSO);
	}
	
	private String id = null;
	private String name = null;
	private int drawableId = 0;
	
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
	
	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}
	
	public int getDrawableId() {
		return drawableId;
	}
}

