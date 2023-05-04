advancement revoke @s only balancedmc:hidden/played_door
execute if score @s LastPlayedDisc matches 0 run advancement grant @s only balancedmc:adventure/putting_together_the_pieces
scoreboard players set @s LastPlayedDisc 1
