import { mapEnterToButton, alertMsg, workWithLoading, getLocation } from './common.js'

export function setup() {
    const createClassroomForm = document.querySelector('#createClassroomForm')
    if (createClassroomForm) {
        const createClassroomButton = createClassroomForm.querySelector('#createClassroomButton')
        const classroomName = createClassroomForm.querySelector('#classroomName')
        const classroomDescription = createClassroomForm.querySelector('#classroomDescription')

        createClassroomButton.addEventListener('click', () => createClassroom(createClassroomButton, classroomName, classroomDescription))
        classroomName.addEventListener('keyup', (event) => {
            classroomName.classList.remove('is-invalid')
            mapEnterToButton(classroomName, event, createClassroomButton)
        })
        classroomDescription.addEventListener('keyup', (event) => {
            classroomDescription.classList.remove('is-invalid')
            mapEnterToButton(classroomDescription, event, createClassroomButton)
        })
    }
}

function createClassroom(createClassroomButton, classroomName, classroomDescription) {
    if (classroomName.value.length == 0) {
        $('#classroomNameFeedback').html('Name can\'t be empty')
        return classroomName.classList.add('is-invalid')
    }

    const name = classroomName.value
    const description = classroomDescription.value
    
    if (name.length > 64) {
        $('#classroomNameFeedback').html('Name too long')
        return classroomName.classList.add('is-invalid')
    }
    if (description.length > 256) return classroomDescription.classList.add('is-invalid')

    createClassroomButton.blur()
    $('#createClassroomForm').modal('hide')

    workWithLoading(
        fetch(getLocation(), { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: name,
                description: description
            })
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasCreated) alertMsg(res.message)
                else {
                    location.href = res.message
                }

            })
            .catch(err => alertMsg('Failed to create classroom')))
}