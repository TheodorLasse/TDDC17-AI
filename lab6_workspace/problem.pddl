(define (problem ninenine_problems) (:domain shakeys_world)
(:objects 
    r1 r2 - room
    s1 - small_thing
)

(:init
    (box_in_room r2)
    (shakey_in_room r1)
    (wide_door_connecting r1 r2)
    (wide_door_connecting r2 r1)
    (light_switch_in_room r1)
    (light_switch_in_room r2)
    (small_thing_in_room r1 s1)
)

(:goal (and
    (small_thing_in_room r2 s1) (light_switch_on r1) (light_switch_on r2) (box_in_room r2)
))

;un-comment the following line if metric is needed
;(:metric minimize (???))
)
