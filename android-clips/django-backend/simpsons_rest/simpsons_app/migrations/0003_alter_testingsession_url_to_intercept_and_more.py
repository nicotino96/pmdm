# Generated by Django 4.0.5 on 2022-07-27 07:53

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('simpsons_app', '0002_testingsession_url_to_intercept_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='testingsession',
            name='url_to_intercept',
            field=models.CharField(max_length=50, null=True),
        ),
        migrations.AlterField(
            model_name='testingsession',
            name='url_to_intercept_new_response_code',
            field=models.IntegerField(null=True),
        ),
    ]
