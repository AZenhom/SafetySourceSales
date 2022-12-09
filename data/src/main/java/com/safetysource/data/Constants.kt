package com.safetysource.data

class Constants {
    companion object {

        // Firestore Collections
        const val COLLECTION_ADMIN = BuildConfig.COLLECTION_ADMIN
        const val COLLECTION_PRODUCT_CATEGORY = BuildConfig.COLLECTION_PRODUCT_CATEGORY
        const val COLLECTION_PRODUCT_ITEM = BuildConfig.COLLECTION_PRODUCT_ITEM
        const val COLLECTION_PRODUCT = BuildConfig.COLLECTION_PRODUCT
        const val COLLECTION_RETAILER = BuildConfig.COLLECTION_RETAILER
        const val COLLECTION_RETAILER_REPORT = BuildConfig.COLLECTION_RETAILER_REPORT
        const val COLLECTION_TEAM = BuildConfig.COLLECTION_TEAM
        const val COLLECTION_TEAM_REPORT = BuildConfig.COLLECTION_TEAM_REPORT
        const val COLLECTION_TRANSACTION = BuildConfig.COLLECTION_TRANSACTION
        const val COLLECTION_OFFER = BuildConfig.COLLECTION_OFFER
        const val COLLECTION_SUBSCRIBED_OFFER = BuildConfig.COLLECTION_SUBSCRIBED_OFFER

        // Firebase Storage Folders
        const val FOLDER_RETAILER_PROFILE = BuildConfig.FOLDER_RETAILER_PROFILE
        const val FOLDER_PRODUCT_CATEGORIES = BuildConfig.FOLDER_PRODUCT_CATEGORIES
        const val FOLDER_PRODUCTS = BuildConfig.FOLDER_PRODUCTS
        const val FOLDER_OFFERS = BuildConfig.FOLDER_OFFERS

        // Firestore document attributes
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}