# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Tappearances(models.Model):
    characterid = models.ForeignKey('Tcharacters', models.DO_NOTHING, db_column='characterId')  # Field name made lowercase.
    clipid = models.ForeignKey('Tclips', models.DO_NOTHING, db_column='clipId')  # Field name made lowercase.
    startmilliseconds = models.IntegerField(db_column='startMilliseconds')  # Field name made lowercase.
    endmilliseconds = models.IntegerField(db_column='endMilliseconds')  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'TAppearances'


class Tcharacters(models.Model):
    name = models.CharField(max_length=100)
    surname = models.CharField(blank=True, null=True, max_length=100)
    description = models.CharField(blank=True, null=True, max_length=500)
    imageurl = models.CharField(db_column='imageUrl', blank=True, null=True, max_length=500)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'TCharacters'


class Tclips(models.Model):
    title = models.CharField(max_length=100)
    videourl = models.CharField(db_column='videoUrl', blank=True, null=True, max_length=500)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'TClips'

class TestingSession(models.Model):
    timestamp = models.CharField(max_length=20)
    dev_code = models.CharField(max_length=3)
    url_to_intercept = models.CharField(max_length=50, null=True)
    url_to_intercept_new_response_code = models.IntegerField(null=True)

class TestingRequest(models.Model):
    method = models.CharField(max_length=7)
    url = models.CharField(max_length=255)
    session = models.ForeignKey(TestingSession, on_delete=models.CASCADE)
