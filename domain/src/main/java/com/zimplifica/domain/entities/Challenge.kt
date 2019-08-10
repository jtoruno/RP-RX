package com.zimplifica.domain.entities

enum class Level {
    Bronze, Silver, Gold, Diamond
}
enum class ChallengeType {
    Specific, General
}

class Challenge(val name: String,val description: String,val esName: String,val esDescription: String,val isCompleted: Boolean,val rewards: Double,val type: ChallengeType,val progressLevel: Double){
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Challenge) return false
        return name == other.name && description == other.description
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + esName.hashCode()
        result = 31 * result + esDescription.hashCode()
        result = 31 * result + isCompleted.hashCode()
        result = 31 * result + rewards.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + progressLevel.hashCode()
        return result
    }
}

class Benefit(val levelAssociated: Level,val name: String,val description: String, esName: String,val esDescription: String)

class UserLevel(val level: Level,val benefits: List<Benefit>,val isCurrentLevel: Boolean){
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is UserLevel) return false
        return level == other.level
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + benefits.hashCode()
        result = 31 * result + isCurrentLevel.hashCode()
        return result
    }
}