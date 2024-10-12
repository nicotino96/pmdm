"""sessions_api URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.urls import path
from rest_app import endpoints

urlpatterns = [
    #########
    # TESTING
    # Same real-endpoints, but prefixed with testing/<dev_code>
    path('testing/<dev_code>/users/<username>/status', endpoints.test_status),
    path('testing/<dev_code>/users', endpoints.test_register),
    path('testing/<dev_code>/sessions', endpoints.test_login),
    # POST new testing session or GET all saved requests for one session
    path('testing/<dev_code>/<timestamp>', endpoints.test_session),
    # DELETE all testing sessions
    path('testing/<dev_code>', endpoints.test_session_delete),
    
    ################
    # REAL ENDPOINTS
    path('users/<username>/status', endpoints.status),
    path('users', endpoints.register),
    path('sessions', endpoints.login),
]
