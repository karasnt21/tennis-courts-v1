package com.tenniscourts.guests;

import com.tenniscourts.exceptions.BusinessException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private GuestMappter guestMapper;

    public List<GuestDTO> findAll() {
        return this.guestMapper.map(this.guestRepository.findAll());
    }

    public GuestDTO findById(Long id) {
        return this.guestRepository.findById(id).map(guestMapper::map)
                        .orElseThrow(() -> new EntityNotFoundException("Guest not found for ID=" + id));
    }

    public Boolean updateGuest(GuestDTO guest) {
        isGuestExists(guest.getId());
        guestRepository.saveAndFlush(guestMapper.map(guest));
        return true;
    }

    private void isGuestExists(Long id) {
        Optional<Guest> guestOptional = this.guestRepository.findById(id);
        if (guestOptional.isEmpty()) {
            throw new EntityNotFoundException("No guest entity was found with the specified id");
        }
    }

    public Boolean deleteById(Long id) {
        isGuestExists(id);
         this.guestRepository.deleteById(id);
         return true;

    }

    public Long create(GuestDTO guest) {
        if (guest.getId() > 0) {
            throw new BusinessException("Guest id should be zero.");
        }
        return guestRepository.saveAndFlush(guestMapper.map(guest)).getId();
    }

    public List<GuestDTO> findGuestByName(String name) {
        return this.guestMapper.map(this.guestRepository.findByNameContainingIgnoreCase(name));
    }
}

