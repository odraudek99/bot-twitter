package mx.com.odraudek99.bot.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="usuarios")
public class Persona {

	public Persona(String usuario) {
		super();
		this.usuario = usuario;
	}

	public Persona(){}
	
	
	private String usuario;
	@Id
	private Long idTwitter;
	
	public Long getIdTwitter() {
		return idTwitter;
	}

	public void setIdTwitter(Long idTwitter) {
		this.idTwitter = idTwitter;
	}


	private Date fechaResponse;
	
	private List<String> twitts;
	
	
	

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFechaResponse() {
		return fechaResponse;
	}

	public void setFechaResponse(Date fechaResponse) {
		this.fechaResponse = fechaResponse;
	}

	public List<String> getTwitts() {
		
		if (twitts == null) {
			twitts = new ArrayList<String>();
			
		}
		return twitts;
	}

	public void setTwitts(List<String> twitts) {
		this.twitts = twitts;
	}
	
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		for (String twitt: getTwitts()) {
			sb.append(twitt).append(", ");
		}
		
		return "{idTwitter: "+idTwitter+", usuario: "+usuario+", fechaResponse: "+fechaResponse+", twitts: ["+
					sb.toString() +
					"] }";
	}
	
	
	
}
