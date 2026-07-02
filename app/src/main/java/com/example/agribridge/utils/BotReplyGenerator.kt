package com.example.agribridge.utils

object BotReplyGenerator {
    
    private val agricultureReplies = listOf(
        
        "Crop insurance protects farmers from losses caused by droughts, floods, pests, and other natural disasters.",
        
        "Regular soil testing helps determine nutrient requirements and improves crop productivity.",
        
        "Drip irrigation can save up to 50% of water compared to traditional irrigation methods.",
        
        "Using certified seeds improves germination rates and increases overall crop yield.",
        
        "Crop rotation helps maintain soil fertility and reduces pest infestations.",
        
        "Organic farming improves soil health and reduces dependence on chemical fertilizers.",
        
        "Timely weed control is essential for achieving better crop production.",
        
        "Integrated Pest Management (IPM) helps reduce pesticide usage while controlling pests effectively.",
        
        "Weather forecasts should be checked before irrigation, sowing, or pesticide spraying.",
        
        "Balanced NPK fertilizers provide essential nutrients required for healthy crop growth.",
        
        "Green manure crops help improve soil structure and organic matter content.",
        
        "Mulching helps conserve soil moisture and suppress weed growth.",
        
        "PM-KISAN is a government scheme that provides financial assistance to eligible farmers.",
        
        "Proper drainage is important to prevent waterlogging and root diseases.",
        
        "Harvesting crops at the right maturity stage improves quality and market value.",
        
        "Soil organic carbon plays a major role in maintaining long-term soil fertility.",
        
        "Precision farming techniques help optimize fertilizer and water usage.",
        
        "Regular crop monitoring helps detect diseases at an early stage.",
        
        "Water management is one of the most important factors affecting crop yield.",
        
        "Sustainable farming practices improve productivity while protecting natural resources."
    )
    
    fun getRandomReply(): String {
        return agricultureReplies.random()
    }
}