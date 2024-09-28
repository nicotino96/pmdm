"""simpsons_rest URL Configuration

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
from simpsons_app import endpoints

urlpatterns = [
    #########
    # TESTING
    # Same real-endpoints, but prefixed with testing/<dev_code>
    path('testing/<dev_code>/clips/<int:clip_id>/appearances', endpoints.test_appearances),
    path('testing/<dev_code>/clips', endpoints.test_clips),
    path('testing/<dev_code>/health', endpoints.test_health),
    # POST new testing session or GET all saved requests for one session
    path('testing/<dev_code>/<timestamp>', endpoints.test_session),
    # DELETE all testing sessions
    path('testing/<dev_code>', endpoints.test_session_delete),
    
    ################
    # REAL ENDPOINTS
    path('clips/<int:clip_id>/appearances', endpoints.appearances),
    path('clips', endpoints.clips),
    path('health', endpoints.health),
]
