package AUSHOP.Controllers.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import AUSHOP.Model.KhachHangDtoModel;
import AUSHOP.entity.KhachHang;
import AUSHOP.repository.KhachHangRepository;


@Controller
@RequestMapping("/admin/account")
public class AccountController {
	
	@Autowired
	KhachHangRepository khachhangRepository;
	
	@Autowired
	BCryptPasswordEncoder bcryptPass;
	
	@RequestMapping("")
	public ModelAndView thongtinadmin (ModelMap model,Principal principal) {
	
		KhachHang kh = khachhangRepository.findByEmail(principal.getName()).get();
		model.addAttribute("user", kh);
		
		return new ModelAndView("/admin/thongtin",model);
	}
	
	@GetMapping("/edit")
	public ModelAndView getEdit(ModelMap model, Principal principal) {
		
		model.addAttribute("info", khachhangRepository.findByEmail(principal.getName()).get());
		
		return new ModelAndView("/admin/edit",model);
	}
	
	@PostMapping("/edit")
	public ModelAndView edit(ModelMap model, @Valid @ModelAttribute("info") KhachHangDtoModel dto, BindingResult result,
			@RequestParam("photo") MultipartFile photo,Principal principal) throws IOException {
		
		if (result.hasErrors()) {
			System.out.println("Lỗi!!!!!!!!!"); 
			//return new ModelAndView("/admin/", model);
		}
		
		KhachHang kh = khachhangRepository.findByEmail(principal.getName()).get();
		if (!photo.getOriginalFilename().equals("")) {

			upload(photo,"/uploads/customers");

			kh.setHinhanhKH(photo.getOriginalFilename());
		}
		kh.setHoTen(dto.getHoTen());
		kh.setGioiTinh(dto.isGioiTinh());
		kh.setSdt(dto.getSdt());
		kh.setDiaChi(dto.getDiaChi());
		
		khachhangRepository.save(kh);
		
		return new ModelAndView("forward:/admin/account");
	}
	
//	public void upload(MultipartFile file, String dir) throws IOException {
//		Path path = Paths.get(dir);
//		InputStream inputStream = file.getInputStream();
//		Files.copy(inputStream, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
//	}
	public void upload(MultipartFile file, String dir) throws IOException {
	    // Kiểm tra tính hợp lệ của đường dẫn được cung cấp từ người dùng
	    if (!isValidDirectory(dir)) {
	        throw new IllegalArgumentException("Đường dẫn không hợp lệ.");
	    }
	    
	    Path path = Paths.get(dir);
	    InputStream inputStream = file.getInputStream();
	    Files.copy(inputStream, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
	}

	// Phương thức kiểm tra tính hợp lệ của đường dẫn
	private boolean isValidDirectory(String dir) {
	    // Kiểm tra xem đường dẫn có tồn tại và là một thư mục không
	    Path path = Paths.get(dir);
	    return Files.exists(path) && Files.isDirectory(path);
	}

}
