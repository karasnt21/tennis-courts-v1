package com.tenniscourts.guests;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
@RestController
@RequestMapping("guest")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @GetMapping("/all")
    public ResponseEntity< List<GuestDTO>> getAllGuests() {
    	
        return ResponseEntity.ok(guestService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable Long id) {
    	
        return ResponseEntity.ok(guestService.findById(id));
    }


    @GetMapping()
    public ResponseEntity<List<GuestDTO>> getGuestByName(@RequestParam String name) {
    	
        return ResponseEntity.ok(guestService.findGuestByName(name));
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> update(@RequestBody @Valid GuestDTO  guest) {
    	
        return ResponseEntity.ok(guestService.updateGuest(guest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable Long  id) {
    	
        return ResponseEntity.ok(guestService.deleteById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody @Valid GuestDTO  guest) {
    	
        return ResponseEntity.ok(guestService.create(guest));
    }

}
