;Header and description

(define (domain shakeys_world)

(:requirements :strips :typing :equality :adl)

(:types
room
small_thing
)

(:predicates 
    (light_switch_in_room ?r -room) ;there is a light switch in the room
    (box_in_room ?r - room) ;there is a box in the room
    (box_under_switch ?r - room) ;there is a box positioned under the light switch in the room
    (small_thing_in_room ?r - room ?s - small_thing) ;a given small thing is in the room
    (wide_door_connecting ?r1 ?r2 - room) ;a wide door connects r1 to r2, not the other way around though! (i.e. not r2 to r1)
    (narrow_door_connecting ?r1 ?r2 - room) ;same as above
    (light_switch_on ?r - room) ;the light is on in a given room
    (shakey_in_room ?r - room) ;shakey is in the given room
    (holding_left ?s) ;shakey is holding something in his left hand
    (holding_right ?s) ;same as above, but right hand
)

;define actions here

(:action move ;moves shakey from one room to another
    :parameters (?from ?to - room)
    :precondition (and (shakey_in_room ?from) 
    (or (narrow_door_connecting ?from ?to) (wide_door_connecting ?from ?to)))
    :effect (and (shakey_in_room ?to)
                (not (shakey_in_room ?from)))
)

(:action move_box_to_room ;moves a box and shakey from one room to another
    :parameters (?from ?to - room)
    :precondition (and (shakey_in_room ?from) (wide_door_connecting ?from ?to)
    (box_in_room ?from) (not (box_in_room ?to)))
    :effect (and (shakey_in_room ?to) (not (shakey_in_room ?from)) 
    (box_in_room ?to) (not (box_in_room ?from)) (not (box_under_switch ?from)))
)

(:action move_box_under_switch ;positions the box under the switch
    :parameters (?r - room)
    :precondition (and (shakey_in_room ?r) (box_in_room ?r) 
    (light_switch_in_room ?r))
    :effect (and (box_under_switch ?r))
)

(:action pick_up_left ;picks up a small object with the left gripper
    :parameters (?s - small_thing ?r - room)
    :precondition (and (shakey_in_room ?r) (small_thing_in_room ?r ?s) 
    (not (holding_left ?s)) (light_switch_on ?r))
    :effect (and (holding_left ?s) (not (small_thing_in_room ?r ?s)))
)

(:action pick_up_right ;pick up a small object with the right gripper
    :parameters (?s - small_thing ?r - room)
    :precondition (and (shakey_in_room ?r) (small_thing_in_room ?r ?s) 
    (not (holding_right ?s)) (light_switch_on ?r))
    :effect (and (holding_right ?s) (not (small_thing_in_room ?r ?s)))
)

(:action drop_left ;drops the object held in the left gripper
    :parameters (?r - room, ?s - small_thing)
    :precondition (and (shakey_in_room ?r) (holding_left ?s))
    :effect (and (small_thing_in_room ?r ?s) (not (holding_left ?s)))
)

(:action drop_right ;drops the object held in the right gripper
    :parameters (?r - room, ?s - small_thing)
    :precondition (and (shakey_in_room ?r) (holding_right ?s))
    :effect (and (small_thing_in_room ?r ?s) (not (holding_right ?s)))
)

(:action turn_on_light ;turn on the light in the room
    :parameters (?r - room)
    :precondition (and (box_under_switch ?r) (shakey_in_room ?r)
    (not (light_switch_on ?r)))
    :effect (and (light_switch_on ?r))
)

(:action turn_off_light ;turn off the light in the room
    :parameters (?r - room)
    :precondition (and (box_under_switch ?r) (shakey_in_room ?r)
    (light_switch_on ?r))
    :effect (and (not (light_switch_on ?r)))
)
)