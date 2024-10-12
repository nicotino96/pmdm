from django.db import models

class CustomUser(models.Model):
	id = models.AutoField(primary_key=True, editable=False)
	username = models.CharField(unique=True, max_length=50)
	encrypted_password = models.CharField(max_length=100)
	status = models.CharField(max_length=200, default="Hey there! I'm using Sessions")

	def __str__(self):
		return self.username

class UserSession(models.Model):
	id = models.AutoField(primary_key=True, editable=False)
	user = models.ForeignKey(CustomUser, on_delete=models.CASCADE)
	token = models.CharField(unique=True, max_length=20)

	def __str__(self):
		return str(self.user) + ' - ' + self.token
		
class TestingSession(models.Model):
    timestamp = models.CharField(max_length=20)
    dev_code = models.CharField(max_length=3)
    url_to_intercept = models.CharField(max_length=50, null=True)
    url_to_intercept_new_response_code = models.IntegerField(null=True)

class TestingRequest(models.Model):
    method = models.CharField(max_length=7)
    url = models.CharField(max_length=255)
    body = models.CharField(max_length=2000)
    session = models.ForeignKey(TestingSession, on_delete=models.CASCADE)
