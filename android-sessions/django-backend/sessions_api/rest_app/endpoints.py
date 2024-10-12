from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import CustomUser, UserSession, TestingSession, TestingRequest
import bcrypt, json, secrets

SESSION_TOKEN_HEADER='Session-Token'

@csrf_exempt
def register(request):
	if request.method != 'POST':
		return JsonResponse({'error':'Unsupported HTTP method'}, status=405)
	body = json.loads(request.body)
	new_username = body.get('username', None)
	if new_username == None:
		return JsonResponse({'error': 'Missing username in request body'}, status=400)
	try:
		CustomUser.objects.get(username=new_username)
	except CustomUser.DoesNotExist:
		# Proceed
		new_password = body.get('password', None)
		if new_password == None:
			return JsonResponse({'error': 'Missing password in request body'}, status=400)

		encrypted_pass = bcrypt.hashpw(new_password.encode('utf8'), bcrypt.gensalt()).decode('utf8')
		new_user = CustomUser()
		new_user.username = new_username
		new_user.encrypted_password = encrypted_pass
		new_user.save()
		return JsonResponse({'created':'True'}, status=201)

	# User DOES exist.
	return JsonResponse({'error': 'User with given username already exists'}, status=409)
	
@csrf_exempt
def login(request):
	if request.method != 'POST':
		return JsonResponse({'error':'Unsupported HTTP method'}, status=405)
	body = json.loads(request.body)
	username = body.get('username', None)
	if username == None:
		return JsonResponse({'error': 'Missing username in request body'}, status=400)
	try:
		user = CustomUser.objects.get(username=username)
	except CustomUser.DoesNotExist:
		return JsonResponse({'error': 'Username does not exist'}, status=404)
	password = body.get('password', None)
	if password == None:
		return JsonResponse({'error': 'Missing password in request body'}, status=400)
	if bcrypt.checkpw(password.encode('utf8'), user.encrypted_password.encode('utf8')):
		new_session = UserSession()
		new_session.user = user
		new_session.token = secrets.token_hex(10)
		new_session.save()
		return JsonResponse({'created':'True', 'sessionId': new_session.id, 'sessionToken': new_session.token }, status=201)
	else:
		return JsonResponse({'error': 'Password is invalid'}, status=401)

@csrf_exempt
def status(request, username):
	try:
		user = CustomUser.objects.get(username=username)
	except CustomUser.DoesNotExist:
		return JsonResponse({'error': 'Username does not exist'}, status=404)
	if user != __get_logged_user(request):
		return JsonResponse({'error': 'Missing token header'}, status=403)
	# Response
	if request.method == 'GET':
		return JsonResponse({'status': user.status}, status=200)
	elif request.method == 'PUT':
		body = json.loads(request.body)
		new_status = body.get('status', None)
		if new_status == None:
			return JsonResponse({'error': 'Missing newStatus in request body'}, status=400)
		user.status = new_status
		user.save()
		return JsonResponse({'updated': 'ok'}, status=200)
	else:
		return JsonResponse({'error':'Unsupported HTTP method'}, status=405)

def __get_logged_user(request):
	session_token = request.headers.get(SESSION_TOKEN_HEADER, None)
	if session_token == None:
		return None
	try:
		session = UserSession.objects.get(token=session_token)
		return session.user
	except UserSession.DoesNotExist:
		return None
		
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
                         url_to_intercept_new_response_code=json_body.get("newResponseCode", None))
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
      response.append({ "method": r.method, "url": r.url, "body": r.body })
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

@csrf_exempt
def test_register(request, dev_code):
  mock_or_error_response = __record_request_and_get_mock_or_error_response(request, dev_code)
  return register(request) if mock_or_error_response is None else mock_or_error_response

@csrf_exempt
def test_login(request, dev_code):
  mock_or_error_response = __record_request_and_get_mock_or_error_response(request, dev_code)
  return login(request) if mock_or_error_response is None else mock_or_error_response
  
@csrf_exempt
def test_status(request, dev_code, username):
  mock_or_error_response = __record_request_and_get_mock_or_error_response(request, dev_code)
  return status(request, username) if mock_or_error_response is None else mock_or_error_response

def __record_request_and_get_mock_or_error_response(request, dev_code):
  session = __store_request(request, dev_code)
  if session is None:
    return JsonResponse({"error": "recent testing session cannot be found"}, status=404)
  if session.url_to_intercept is not None:
    real_intercept_path = "/testing/" + session.dev_code + session.url_to_intercept
    if real_intercept_path == request.path:
      return JsonResponse({"response": "mocked response"}, status=session.url_to_intercept_new_response_code)
  return None

def __store_request(request, dev_code):
  sessions = TestingSession.objects.filter(dev_code=dev_code).order_by('-timestamp')[:1]
  if len(sessions) == 0:
    return None
  t = TestingRequest(method=request.method, url=request.build_absolute_uri(), body=request.body.decode('utf-8'), session=sessions[0])
  t.save()
  return sessions[0]


