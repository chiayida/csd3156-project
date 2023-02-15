package edu.singaporetech.services

open class Vector2( var x: Float = 0F, var y: Float = 0F){

    fun copy(x: Float = this.x, y: Float = this.y) =
        Vector2(x, y)

    fun toZero() {
        x = 0F
        y = 0F
    }

    fun VectoString() : String{
        return "($x, $y)"
    }
}

// Parent class that is to be inherited by other classes
open class Entity {
    var position: Vector2 = Vector2(0F, 0F)
    var speed: Float = 1F
    var xScale: Float = 1F
    var yScale: Float = 1F
    var velocity: Vector2 = Vector2(0F, 0F)
    var colliderScale : Vector2 = Vector2(1F, 1F)
    var tag: String = ""

    fun getColliderMax() : Vector2 {
        var max = Vector2(position.x, position.y)
        max.x += colliderScale.x
        max.y += colliderScale.y
        return max
    }

    fun getColliderMin() : Vector2 {
        var min = Vector2(position.x, position.y)
        min.x -= colliderScale.x
        min.y -= colliderScale.y
        return min
    }

    fun updatePosition(dt : Float) {
        position.x += velocity.x * dt
        position.y += velocity.y * dt
    }
}