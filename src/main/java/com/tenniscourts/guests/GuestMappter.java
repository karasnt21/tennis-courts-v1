package com.tenniscourts.guests;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GuestMappter {

    Guest map(GuestDTO guestDTO);

    GuestDTO map (Guest guest);

    List<GuestDTO> map(List<Guest> guests);
}
