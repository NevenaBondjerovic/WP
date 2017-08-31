package app.theme;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.CommonMethods;
import app.comment.Comment;
import app.user.User;

@RestController
@RequestMapping("/themes")
public class ThemeController {

	private final ThemeService themeService;
	private final ServletContext context; 
	private CommonMethods common;

	@Autowired
	public ThemeController(final ThemeService themeService,final ServletContext context) {
		this.themeService = themeService;
		this.context = context;
		this.common = new CommonMethods();
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Theme> findAll(){
		return themeService.findAll();
	}
	
	@GetMapping(params="id")
	public synchronized ResponseEntity<Object> findOne(@PathParam("id") String id){
		Theme theme = themeService.findOne(id);
		Optional.ofNullable(theme).orElseThrow(()-> new ResourceNotFoundException("Theme not found!"));
		return new ResponseEntity<Object>(theme, HttpStatus.OK);
	}
	
	@GetMapping(params="forumName")
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Theme> findByForum(@PathParam("forumName") String forumName){
		return themeService.findByForum(forumName);
	}
	

	@PostMapping
	public synchronized ResponseEntity<Object> save(@RequestBody Theme theme){
		if(theme.getForum()!=null && theme.getForum().getName()!=null && theme.getTitle()!=null)
			theme.setId(common.setText(theme.getForum().getName())+" "
					+common.setText(theme.getTitle()));
		if(validate(theme)){
			Theme t = themeService.save(theme);
			if(t==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(t, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping
	public synchronized ResponseEntity<Object> update(@RequestBody Theme theme){
		if(validate(theme)){
			Theme t = themeService.update(theme);
			if(t==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(t, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(params="themeId")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public synchronized void delete(@PathParam("themeId") String themeId){
		if(themeService.delete(themeId)==false){
			throw new ResourceNotFoundException("Theme not found.");
		}
	}
	
	@RequestMapping(path = "/upload", method = RequestMethod.POST)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ResponseStatus(HttpStatus.CREATED)
	public synchronized String handleFileUpload(@RequestParam("file") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		
		InputStream inputStream =  new BufferedInputStream(multipartFile.getInputStream());

		String filePath = context.getRealPath("");
        try
        {
            int read = 0;
            byte[] bytes = new byte[1024];
            
            File file = new File(filePath+File.separator+"userImages"+File.separator+multipartFile.getOriginalFilename());
            OutputStream out = new FileOutputStream(file);
         
            while ((read = inputStream.read(bytes)) != -1) 
            {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) 
        {
            throw new WebApplicationException();
        }
        return "userImages/"+multipartFile.getOriginalFilename();
	}
	
	
	/**
	 * Proverada da li prosledjena {@link Theme} ima sve elemente koje treba da ima.
	 */
	private Boolean validate(Theme t){
		if(t==null || 
				(t!=null && (t.getId()==null || t.getForum()==null || t.getForum().getName()==null 
					|| t.getTitle()==null || t.getAuthor()==null || t.getAuthor().getUsername()==null 
					|| t.getText()==null || t.getType()==null))){
			return false;
		}

		if(t.getComments()==null)	
			t.setComments(new ArrayList<Comment>());
		if(t.getDate()==null){
			Date today = new Date();
			String[] parts=today.toString().split(" ");
			String date=parts[2]+"."+stringToNumberMonth(parts[1])+"."+parts[5]+".";
			t.setDate(date);
		}
		if(t.getLikes()==null)
			t.setLikes(0);
		if(t.getDislikes()==null)
			t.setDislikes(0);
		if(t.getLikeUsers()==null)	
			t.setLikeUsers(new ArrayList<User>());
		if(t.getDislikeUsers()==null)	
			t.setDislikeUsers(new ArrayList<User>());
		if(!t.getType().equals(ThemeType.TEXT) && t.getContent()==null)
			return false;
		return true;
	}
	

	private String stringToNumberMonth(String month){
		if(month.equals("Jan"))
			return "01";
		else if(month.equals("Feb"))
			return "02";
		else if(month.equals("Mar"))
			return "03";
		else if(month.equals("Apr"))
			return "04";
		else if(month.equals("May"))
			return "05";
		else if(month.equals("Jun"))
			return "06";
		else if(month.equals("Jul"))
			return "07";
		else if(month.equals("Aug"))
			return "08";
		else if(month.equals("Sep"))
			return "09";
		else if(month.equals("Oct"))
			return "10";
		else if(month.equals("Nov"))
			return "11";
		else if(month.equals("Dec"))
			return "12";
		return null;
	}
	
	
}
