from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import Tclips, Tappearances, Tcharacters, TestingSession, TestingRequest
import json

def appearances(request, clip_id):
  if request.method != 'GET':
    return JsonResponse({"error": "HTTP method not allowed"}, status=405)
  try:
    milliseconds = request.GET['milliseconds']
  except KeyError:
    return JsonResponse({"error": "Missing 'milliseconds' query parameter"}, status=400)
  try:
    clip = Tclips.objects.get(pk=clip_id)
  except Tclips.DoesNotExist:
    return JsonResponse({"error": "Clip with id " + str(clip_id) + " does not exist"}, status=404)
  appearances = Tappearances.objects.filter(clipid=clip_id, startmilliseconds__lte=milliseconds, endmilliseconds__gte=milliseconds)
  response = []
  for appearance in appearances:
    character = appearance.characterid
    response.append({"name": character.name,
                     "surname": character.surname,
                     "description": character.description,
                     "imageUrl": character.imageurl })
  return JsonResponse(response, safe=False, status=200)

def clips(request):
  if request.method != 'GET':
    return JsonResponse({"error": "HTTP method not allowed"}, status=405)
  response = []
  for clip in Tclips.objects.all():
    response.append({ "id": clip.id,
                      "title": clip.title,
                      "videoUrl": clip.videourl })
  return JsonResponse(response, safe=False, status=200)

def health(request):
  if request.method != 'GET':
    return JsonResponse({"error": "HTTP method not allowed"}, status=405)
  return JsonResponse({"status": "Server is healthy!"})

def status(request):
  if request.method != 'GET':
    return JsonResponse({"error": "HTTP method not allowed"}, status=405)
  return JsonResponse({"status": "Server is up!"})


#################
# TESTING RELATED

@csrf_exempt
def test_session(request, dev_code, timestamp):
  if request.method == 'POST':
    if len(request.body) == 0:
      t = TestingSession(dev_code=dev_code, 
                         timestamp=timestamp)
    else:
      json_body = json.loads(request.body)
      t = TestingSession(dev_code=dev_code,
                         timestamp=timestamp,
                         url_to_intercept=json_body.get("urlToIntercept", None),
                         url_to_intercept_new_response_code=json_body.get("newResponseCode", None),)
    # Begin new session
    t.save()
    return JsonResponse({"status": "created"}, status=201)
  elif request.method == 'GET':
    # Get session stored requests 
    sessions = TestingSession.objects.filter(dev_code=dev_code).order_by('-timestamp')[:1]
    if len(sessions) == 0:
      return JsonResponse([], safe=False)
    session_requests = sessions[0].testingrequest_set
    response = []
    for r in session_requests.all():
      response.append({ "method": r.method, "url": r.url })
    return JsonResponse(response, safe=False)  
  else:
    return JsonResponse({"error": "Not allowed"}, status=405)

@csrf_exempt
def test_session_delete(request, dev_code): 
  if request.method == 'DELETE':
    # Finish sessions (ALL of them, why keep stalled sessions?)
    t = TestingSession.objects.filter(dev_code=dev_code)
    t.delete() # CASCADE will remove TestingRequests also
    return JsonResponse({"status": "removed"}, status=200)
    
def test_appearances(request, dev_code, clip_id):
  mock_or_error_response = __record_request_and_get_mock_or_error_response(request, dev_code)
  return appearances(request, clip_id) if mock_or_error_response is None else mock_or_error_response

def test_clips(request, dev_code):
  mock_or_error_response = __record_request_and_get_mock_or_error_response(request, dev_code)
  return clips(request) if mock_or_error_response is None else mock_or_error_response

def test_health(request, dev_code):
  mock_or_error_response = __record_request_and_get_mock_or_error_response(request, dev_code)
  return health(request) if mock_or_error_response is None else mock_or_error_response

def __record_request_and_get_mock_or_error_response(request, dev_code):
  session = __store_request(request, dev_code)
  if session is None:
    return JsonResponse({"error": "recent testing session cannot be found"}, status=404)
  if session.url_to_intercept is not None:
    real_intercept_path = "/testing/" + session.dev_code + session.url_to_intercept
    print(real_intercept_path)
    print(request.path)
    if real_intercept_path == request.path:
      return JsonResponse({"response": "mocked response"}, status=session.url_to_intercept_new_response_code)
  return None

def __store_request(request, dev_code):
  sessions = TestingSession.objects.filter(dev_code=dev_code).order_by('-timestamp')[:1]
  if len(sessions) == 0:
    return None
  t = TestingRequest(method=request.method, url=request.build_absolute_uri(), session=sessions[0])
  t.save()
  return sessions[0]


  
